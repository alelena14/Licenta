import tensorflow as tf
from keras import layers, models
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
import os
from keras.applications.efficientnet_v2 import preprocess_input

# =========================
# RESET
# =========================
tf.keras.backend.clear_session()
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'

# =========================
# CONFIG
# =========================
IMG_SIZE = 224
BATCH_SIZE = 32
EPOCHS = 10

DATASET_PATH = "final_dataset/images"
CSV_PATH = "final_dataset/final_labels.csv"

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
NUM_CLASSES = len(CLASS_NAMES)

# =========================
# LOAD DATA
# =========================
df = pd.read_csv(CSV_PATH)

dry_df = df[df["dry_skin"] == 1]
black_df = df[df["blackheads"] == 1]
wrinkle_df = df[df["wrinkles"] == 1]

df = pd.concat([
    df,
    dry_df, dry_df,      # boost dry_skin
    black_df,            # mic boost
    wrinkle_df           # mic boost
])

df = df.sample(frac=1).reset_index(drop=True)

filenames = df["filename"].values
labels = df[CLASS_NAMES].values.astype("float32")

# =========================
# SPLIT
# =========================
train_files, val_files, train_labels, val_labels = train_test_split(
    filenames,
    labels,
    test_size=0.2,
    random_state=42
)


# =========================
# PREPROCESS
# =========================

def parse_image(filename, label):
    img_path = tf.strings.join([DATASET_PATH, filename], separator="/")
    img = tf.io.read_file(img_path)
    img = tf.image.decode_jpeg(img, channels=3)
    img = tf.image.resize(img, (IMG_SIZE, IMG_SIZE))
    img = preprocess_input(img)
    return img, label


# =========================
# DATA PIPELINE
# =========================

train_ds = tf.data.Dataset.from_tensor_slices((train_files, train_labels))
train_ds = train_ds.map(parse_image, num_parallel_calls=tf.data.AUTOTUNE)
train_ds = train_ds.cache()


data_augmentation = tf.keras.Sequential([
    layers.RandomFlip("horizontal"),
    layers.RandomRotation(0.1),
    layers.RandomZoom(0.1),
    layers.RandomContrast(0.2),
    layers.GaussianNoise(0.02),
])

train_ds = train_ds.map(
    lambda x, y: (data_augmentation(x), y),
    num_parallel_calls=tf.data.AUTOTUNE
)

train_ds = train_ds.shuffle(2000).batch(BATCH_SIZE).prefetch(tf.data.AUTOTUNE)

val_ds = tf.data.Dataset.from_tensor_slices((val_files, val_labels))
val_ds = val_ds.map(parse_image, num_parallel_calls=tf.data.AUTOTUNE)
val_ds = val_ds.batch(BATCH_SIZE).prefetch(tf.data.AUTOTUNE)

# =========================
# MODEL
# =========================
base_model = tf.keras.applications.EfficientNetV2B0(
    input_shape=(IMG_SIZE, IMG_SIZE, 3),
    include_top=False,
    weights="imagenet"
)

base_model.trainable = False

inputs = tf.keras.Input(shape=(IMG_SIZE, IMG_SIZE, 3))

x = base_model(inputs, training=False)
x = layers.GlobalAveragePooling2D()(x)
x = layers.BatchNormalization()(x)

x = layers.Dense(512, activation="relu")(x)
x = layers.Dropout(0.4)(x)

outputs = layers.Dense(NUM_CLASSES, activation="sigmoid")(x)

model = models.Model(inputs, outputs)

# =========================
# COMPILE
# =========================
model.compile(
    optimizer=tf.keras.optimizers.Adam(learning_rate=3e-4),
    loss="binary_crossentropy",
    metrics=[
        tf.keras.metrics.BinaryAccuracy(),
        tf.keras.metrics.AUC(name="auc"),
        tf.keras.metrics.Precision(),
        tf.keras.metrics.Recall()
    ]
)

model.summary()

# =========================
# CALLBACKS
# =========================
callbacks = [
    tf.keras.callbacks.EarlyStopping(
        monitor="val_loss",
        patience=4,
        restore_best_weights=True
    ),
    tf.keras.callbacks.ModelCheckpoint(
        "best_multilabel.h5",
        save_best_only=True,
        save_weights_only=True
    ),
]

# =========================
# TRAIN HEAD
# =========================
print("\nTraining Multi-Label...\n")

model.fit(
    train_ds,
    validation_data=val_ds,
    epochs=EPOCHS,
    callbacks=callbacks,
    verbose=1
)

# =========================
# FINE-TUNING
# =========================
print("\nFine-Tuning...\n")

model.load_weights("best_multilabel.h5")

base_model.trainable = True

for layer in base_model.layers[:-100]:
    layer.trainable = False

for layer in base_model.layers:
    if isinstance(layer, tf.keras.layers.BatchNormalization):
        layer.trainable = False

model.compile(
    optimizer=tf.keras.optimizers.Adam(learning_rate=5e-6),
    loss="binary_crossentropy",
    metrics=[
        tf.keras.metrics.BinaryAccuracy(),
        tf.keras.metrics.AUC(name="auc")
    ]
)

model.fit(
    train_ds,
    validation_data=val_ds,
    epochs=8,
    callbacks=callbacks,
    verbose=1
)

# =========================
# SAVE FINAL
# =========================
model.load_weights("best_multilabel.h5")
model.save("final_multilabel_model.keras")

print("\nDONE")