import cv2
import numpy as np
import tensorflow as tf
from keras.applications.efficientnet_v2 import preprocess_input

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
# PREDICT
# =========================
def predict_image(image_path):
    image = cv2.imread(image_path)

    if image is None:
        print(" Image not found")
        return

    inp = preprocess(image)
    preds = model.predict(inp, verbose=0)[0]

    print("\n PREDICTIONS:\n")

    for i, val in enumerate(preds):
        print(f"{CLASS_NAMES[i]}: {val:.3f}")

    print("\n FINAL RESULT:\n")

    selected = np.where(preds >= THRESHOLD)[0]

    if len(selected) == 0:
        best = np.argmax(preds)
        print(f"(fallback) {CLASS_NAMES[best]} ({preds[best]:.3f})")
    else:
        for i in selected:
            print(f"{CLASS_NAMES[i]} ({preds[i]:.3f})")


# =========================
# RUN
# =========================
if __name__ == "__main__":
    predict_image("test_images/left.jpeg")
