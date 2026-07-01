from fastapi import FastAPI, UploadFile, File
import cv2
import numpy as np
import tensorflow as tf
from tensorflow.keras.applications.efficientnet_v2 import preprocess_input
from PIL import Image
import io
import os
import logging
from huggingface_hub import hf_hub_download

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

MODEL_PATH = "/opt/render/project/src/final_multilabel_model.keras"

logger.info(f"Model exists: {os.path.exists(MODEL_PATH)}")
logger.info(f"HF_TOKEN present: {bool(os.environ.get('HF_TOKEN'))}")

if not os.path.exists(MODEL_PATH):
    logger.info("Starting download...")
    result = hf_hub_download(
        repo_id="alelena14/final_multilabel_model",
        filename="final_multilabel_model.keras",
        local_dir="/opt/render/project/src",
        token=os.environ.get("HF_TOKEN")
    )
    logger.info(f"Downloaded to: {result}")
    logger.info(f"File exists after download: {os.path.exists(MODEL_PATH)}")
    logger.info(f"File size: {os.path.getsize(MODEL_PATH)} bytes")

os.chdir("/opt/render/project/src")
logger.info(f"CWD: {os.getcwd()}")
logger.info(f"Files in dir: {os.listdir('.')}")

logger.info("Loading model...")
try:
    model = tf.keras.models.load_model("final_multilabel_model.keras")
    logger.info("Model loaded successfully!")
except Exception as e:
    logger.error(f"Error loading model: {e}")
    raise

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

import time

@app.post("/analyze")
async def analyze(file: UploadFile = File(...)):

    start = time.time()
    logger.info("Request received")

    contents = await file.read()
    logger.info(f"Read file: {time.time() - start:.2f}s")

    image = Image.open(io.BytesIO(contents)).convert("RGB")
    logger.info(f"PIL load: {time.time() - start:.2f}s")

    image = np.array(image)
    logger.info(f"Numpy conversion: {time.time() - start:.2f}s")

    inp = preprocess(image)
    logger.info(f"Preprocess: {time.time() - start:.2f}s")

    infer_start = time.time()
    preds = model.predict(inp, verbose=0)[0]
    logger.info(f"Inference only: {time.time() - infer_start:.2f}s")

    logger.info(f"Total request: {time.time() - start:.2f}s")

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