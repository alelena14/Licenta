import tensorflow as tf
from keras import layers, models
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.utils.class_weight import compute_class_weight
import os
from keras.applications.efficientnet import preprocess_input

# =========================
# RESET
# =========================
tf.keras.backend.clear_session()
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
tf.get_logger().setLevel('ERROR')

# =========================
# CONFIG
# =========================
IMG_SIZE = 224
BATCH_SIZE = 32
EPOCHS = 10

DATASET_PATH = "final_dataset/images"
CSV_PATH = "final_dataset/labels.csv"

# =========================
# LOAD DATA
# =========================
df = pd.read_csv(CSV_PATH)

filenames = df["filename"].astype(str).values
labels = df.drop(columns=["filename"]).values
labels = np.argmax(labels, axis=1)

# =========================
# SPLIT
# =========================
train_files, val_files, train_labels, val_labels = train_test_split(
    filenames,
    labels,
    test_size=0.2,
    stratify=labels,
    random_state=42
)

# =========================
# CLASS WEIGHTS
# =========================
class_weights = compute_class_weight(
    class_weight="balanced",
    classes=np.unique(train_labels),
    y=train_labels
)
class_weights = dict(enumerate(class_weights))

print("Class weights:", class_weights)


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

val_ds = tf.data.Dataset.from_tensor_slices((val_files, val_labels))
val_ds = val_ds.map(parse_image, num_parallel_calls=tf.data.AUTOTUNE)

# =========================
# AUGMENTATION (OUTSIDE MODEL)
# =========================
data_augmentation = tf.keras.Sequential([
    layers.RandomFlip("horizontal"),
    layers.RandomRotation(0.2),
    layers.RandomZoom(0.2),
    layers.RandomContrast(0.2),
])

train_ds = train_ds.map(
    lambda x, y: (data_augmentation(x), y),
    num_parallel_calls=tf.data.AUTOTUNE
)

train_ds = train_ds.shuffle(1000).batch(BATCH_SIZE).prefetch(tf.data.AUTOTUNE)
val_ds = val_ds.batch(BATCH_SIZE).prefetch(tf.data.AUTOTUNE)

# =========================
# BASE MODEL
# =========================
base_model = tf.keras.applications.EfficientNetV2B0(
    input_shape=(IMG_SIZE, IMG_SIZE, 3),
    include_top=False,
    weights="imagenet"
)

base_model.trainable = False

# =========================
# MODEL
# =========================
inputs = tf.keras.Input(shape=(IMG_SIZE, IMG_SIZE, 3))

x = base_model(inputs, training=False)
x = layers.GlobalAveragePooling2D()(x)
x = layers.BatchNormalization()(x)

x = layers.Dense(1024, activation="relu")(x)
x = layers.Dropout(0.5)(x)

x = layers.Dense(512, activation="relu")(x)
x = layers.Dropout(0.3)(x)

outputs = layers.Dense(5, activation="softmax")(x)

model = models.Model(inputs, outputs)

# =========================
# COMPILE (HEAD TRAIN)
# =========================
model.compile(
    optimizer=tf.keras.optimizers.Adam(learning_rate=3e-4),
    loss="sparse_categorical_crossentropy",
    metrics=["accuracy"]
)

# =========================
# CALLBACKS HEAD
# =========================
callbacks_head = [
    tf.keras.callbacks.EarlyStopping(
        monitor="val_loss",
        patience=5,
        restore_best_weights=True
    ),
    tf.keras.callbacks.ModelCheckpoint(
        "models/best_head.h5",
        save_best_only=True,
        save_weights_only=True
    ),
    tf.keras.callbacks.ReduceLROnPlateau(
        monitor="val_loss",
        factor=0.3,
        patience=2,
        min_lr=1e-6
    ),
]

# =========================
# TRAIN HEAD
# =========================
print("\nTraining HEAD...\n")

model.fit(
    train_ds,
    validation_data=val_ds,
    epochs=EPOCHS,
    callbacks=callbacks_head,
    class_weight=class_weights,
    verbose=1
)

# =========================
# FINE-TUNING
# =========================
print("\nFine-Tuning...\n")

model.load_weights("models/best_head.h5")

base_model.trainable = True

for layer in base_model.layers[:-50]:
    layer.trainable = False

for layer in base_model.layers:
    if isinstance(layer, tf.keras.layers.BatchNormalization):
        layer.trainable = False

model.compile(
    optimizer=tf.keras.optimizers.Adam(learning_rate=1e-5),
    loss="sparse_categorical_crossentropy",
    metrics=["accuracy"]
)

callbacks_fine = [
    tf.keras.callbacks.EarlyStopping(
        monitor="val_loss",
        patience=3,
        restore_best_weights=True
    ),
    tf.keras.callbacks.ModelCheckpoint(
        "models/best_finetuned.h5",
        save_best_only=True,
        save_weights_only=True
    ),
]

model.fit(
    train_ds,
    validation_data=val_ds,
    epochs=10,
    callbacks=callbacks_fine,
    class_weight=class_weights,
    verbose=1
)

# =========================
# SAVE FINAL MODEL (FULL)
# =========================
print("\nSaving final model...\n")

model.load_weights("models/best_finetuned.h5")
model.save("models/final_model.keras")

print("\nTRAINING COMPLETE")