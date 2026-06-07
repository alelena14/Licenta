import pandas as pd

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

df = pd.read_csv(CSV_PATH)

print("\n=== CLASS DISTRIBUTION ===\n")

total = len(df)

for cls in CLASS_NAMES:
    count = int(df[cls].sum())
    percentage = count / total * 100

    print(
        f"{cls:20s} "
        f"{count:6d} images "
        f"({percentage:.2f}%)"
    )

print(f"\nTotal images: {total}")