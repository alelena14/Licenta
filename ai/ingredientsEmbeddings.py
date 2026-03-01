from sentence_transformers import SentenceTransformer
from dotenv import load_dotenv
import os
import psycopg2

load_dotenv()

conn = psycopg2.connect(
    host=os.getenv("DB_HOST"),
    database=os.getenv("DB_NAME"),
    user=os.getenv("DB_USER"),
    password=os.getenv("DB_PASSWORD"),
    port=os.getenv("DB_PORT"),
    sslmode=os.getenv("DB_SSLMODE")
)

model = SentenceTransformer("all-MiniLM-L6-v2")

cur = conn.cursor()

cur.execute("SELECT id, name, description, benefits, function, category, comedogenic_rating, irritation_potential FROM ingredients WHERE embedding IS NULL and category IS NOT NULL")
rows = cur.fetchall()

for row in rows:
    ingredient_id = row[0]
    text = f"""
    Name: {row[1]}
    Description: {row[2]}
    Benefits: {row[3]}
    Function: {row[4]}
    Category: {row[5]}
    Comedogenic rating: {row[6]}
    Irritation potential: {row[7]}
    """

    embedding = model.encode(text).tolist()

    cur.execute(
        "UPDATE ingredients SET embedding = %s WHERE id = %s",
        (embedding, ingredient_id)
    )

conn.commit()
cur.close()
conn.close()