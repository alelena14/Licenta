from fastapi import FastAPI, UploadFile, File
import cv2
import numpy as np
import tensorflow as tf
from tensorflow.keras.applications.efficientnet_v2 import preprocess_input
from PIL import Image
import io
import os
from huggingface_hub import hf_hub_download

MODEL_PATH = "final_multilabel_model.keras"

if not os.path.exists(MODEL_PATH):
    hf_hub_download(
        repo_id="alelena14/final_multilabel_model",
        filename="final_multilabel_model.keras",
        local_dir=".",
        token = os.environ.get("HF_TOKEN")
    )

model = tf.keras.models.load_model(MODEL_PATH)

app = FastAPI()

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

def preprocess(img):
    img = cv2.resize(img, (IMG_SIZE, IMG_SIZE))
    img = preprocess_input(img)
    img = np.expand_dims(img, axis=0)
    return img

@app.post("/analyze")
async def analyze(file: UploadFile = File(...)):
    contents = await file.read()
    image = Image.open(io.BytesIO(contents)).convert("RGB")
    image = np.array(image)
    inp = preprocess(image)
    preds = model.predict(inp, verbose=0)[0]

    results = [
        {"label": CLASS_NAMES[i], "confidence": float(val)}
        for i, val in enumerate(preds)
    ]

    selected = [
        {"label": CLASS_NAMES[i], "confidence": float(preds[i])}
        for i in range(len(preds)) if preds[i] >= THRESHOLD
    ]

    if len(selected) == 0:
        best = int(np.argmax(preds))
        selected = [{"label": CLASS_NAMES[best], "confidence": float(preds[best])}]

    return {
        "all_predictions": results,
        "final_predictions": selected
    }