from fastapi import FastAPI, UploadFile, File
import cv2
import numpy as np
import tensorflow as tf
from keras.applications.efficientnet_v2 import preprocess_input
from PIL import Image
import io

app = FastAPI()

# =========================
# CONFIG
# =========================
IMG_SIZE = 224

CLASS_NAMES = [
    "acne",
    "wrinkles",
    "pores",
    "skin_discoloration",
    "blackheads",
    "eyebags",
    "dry_skin",
    "whiteheads"
]

THRESHOLD = 0.4

# =========================
# LOAD MODEL
# =========================
model = tf.keras.models.load_model("final_multilabel_model.keras")


# =========================
# PREPROCESS
# =========================
def preprocess(img):
    img = cv2.resize(img, (IMG_SIZE, IMG_SIZE))
    img = preprocess_input(img)
    img = np.expand_dims(img, axis=0)
    return img


# =========================
# API ENDPOINT
# =========================
@app.post("/analyze")
async def analyze(file: UploadFile = File(...)):
    contents = await file.read()

    # convert bytes → OpenCV image
    image = Image.open(io.BytesIO(contents)).convert("RGB")
    image = np.array(image)

    inp = preprocess(image)
    preds = model.predict(inp, verbose=0)[0]

    results = []

    for i, val in enumerate(preds):
        results.append({
            "label": CLASS_NAMES[i],
            "confidence": float(val)
        })

    selected = [
        {
            "label": CLASS_NAMES[i],
            "confidence": float(preds[i])
        }
        for i in range(len(preds)) if preds[i] >= THRESHOLD
    ]

    if len(selected) == 0:
        best = int(np.argmax(preds))
        selected = [{
            "label": CLASS_NAMES[best],
            "confidence": float(preds[best])
        }]

    return {
        "all_predictions": results,
        "final_predictions": selected
    }
