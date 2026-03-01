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
cur.execute("SELECT id, code, display_name, category FROM concerns WHERE embedding IS NULL")
rows = cur.fetchall()

CONCERN_TEXTS = {

    "acne_inflammatory": """
Inflammatory acne.
Red inflamed pimples, papules, pustules, swelling and bacterial overgrowth.
Associated with clogged pores, excess sebum production and skin inflammation.
Helpful ingredients include salicylic acid (BHA), benzoyl peroxide,
retinoids, azelaic acid, niacinamide and anti-inflammatory agents.
Avoid comedogenic, heavy occlusive and irritating ingredients.
""",

    "acne_comedonal": """
Comedonal acne.
Clogged pores, blackheads, whiteheads, excess sebum and dead skin buildup.
Requires keratolytic exfoliation and oil penetration.
Effective ingredients include salicylic acid (BHA),
beta hydroxy acids, retinoids and azelaic acid.
Avoid comedogenic, fatty, pore-clogging and heavy occlusive ingredients.
""",

    "blackheads": """
Blackheads.
Open clogged pores filled with oxidized sebum.
Associated with excess oil and dead skin accumulation.
Helpful ingredients include salicylic acid, BHA exfoliants,
retinoids and pore-refining actives.
Avoid heavy oils and comedogenic ingredients.
""",

    "whiteheads": """
Whiteheads.
Closed comedones caused by clogged pores and excess sebum.
Improved by salicylic acid (BHA), gentle exfoliation,
retinoids and oil-control ingredients.
Avoid occlusive and pore-clogging ingredients.
""",

    "hormonal_acne": """
Hormonal acne.
Deep cystic breakouts linked to hormonal imbalance and excess sebum.
Common treatments include retinoids, salicylic acid,
benzoyl peroxide, azelaic acid and anti-inflammatory ingredients.
Avoid heavy comedogenic oils and irritants.
""",

    "congested_skin": """
Congested skin.
Clogged pores, uneven texture and buildup of dead skin cells.
Helpful ingredients include exfoliating acids (AHA, BHA),
retinoids and oil-regulating ingredients.
Avoid heavy occlusive and pore-clogging ingredients.
""",

    "oily_skin": """
Oily skin.
Excess sebum production, shine and enlarged pores.
Beneficial ingredients regulate oil production,
mattify skin and control sebum.
Helpful actives include niacinamide, salicylic acid,
clay and oil-absorbing ingredients.
Avoid heavy oils and thick occlusive formulations.
""",

    "combination_skin_oily_tzone": """
Combination skin with oily T-zone.
Excess oil in forehead, nose and chin with normal or dry cheeks.
Helpful ingredients balance oil production
without over-drying the skin.
Beneficial actives include niacinamide,
gentle exfoliants and lightweight hydrators.
""",

    "enlarged_pores": """
Enlarged pores.
Visible pores often linked to excess sebum
and loss of skin elasticity.
Helpful ingredients include salicylic acid,
retinoids, niacinamide and exfoliating acids.
Focus on oil control and texture refinement.
""",

    "sebaceous_filaments": """
Sebaceous filaments.
Oil-filled pores commonly visible on nose and cheeks.
Associated with excess sebum production.
Improved by salicylic acid (BHA),
gentle exfoliation and oil control ingredients.
Avoid heavy occlusive oils.
""",

    "dry_skin": """
Dry skin.
Lack of oil and moisture causing tightness and rough texture.
Beneficial ingredients include ceramides,
glycerin, hyaluronic acid, squalane and nourishing emollients.
Avoid drying alcohols and harsh surfactants.
""",

    "dehydrated_skin": """
Dehydrated skin.
Lack of water content leading to dullness and tightness.
Improved by humectants such as glycerin
and hyaluronic acid along with barrier-supporting ingredients.
Avoid strong drying ingredients.
""",

    "damaged_barrier": """
Damaged skin barrier.
Compromised protective layer causing irritation,
dryness and increased sensitivity.
Helpful ingredients include ceramides,
panthenol, soothing agents and barrier-repair ingredients.
Avoid harsh exfoliants and fragrance.
""",

    "flaky_skin": """
Flaky skin.
Peeling and rough patches due to dryness.
Improved by hydration, gentle exfoliation,
ceramides and nourishing emollients.
Avoid over-exfoliation and drying ingredients.
""",

    "sensitive_skin": """
Sensitive skin.
Reactive skin prone to redness and irritation.
Helpful ingredients include soothing agents,
ceramides, panthenol and anti-inflammatory actives.
Avoid fragrance, strong acids and irritants.
""",

    "irritated_skin": """
Irritated skin.
Redness, burning sensation and inflammation.
Improved by calming and anti-inflammatory ingredients.
Avoid exfoliating acids and fragrance.
""",

    "rosacea_prone": """
Rosacea-prone skin.
Persistent redness, flushing and visible blood vessels.
Helpful ingredients reduce inflammation
and support barrier repair.
Avoid irritants, fragrance and strong exfoliants.
""",

    "eczema_prone": """
Eczema-prone skin.
Severe dryness, itching and inflammation
associated with a weakened skin barrier.
Helpful ingredients include ceramides,
humectants and soothing barrier-repair agents.
Avoid fragrance and harsh surfactants.
""",

    "hyperpigmentation": """
Hyperpigmentation.
Dark spots caused by excess melanin production.
Beneficial ingredients include vitamin C,
niacinamide, azelaic acid and exfoliating acids.
Focus on melanin regulation and brightening.
""",

    "melasma": """
Melasma.
Hormone-related dark patches and uneven pigmentation.
Improved by vitamin C, azelaic acid,
niacinamide and gentle exfoliation.
Sun protection is essential.
""",

    "post_inflammatory_hyperpigmentation": """
Post-inflammatory hyperpigmentation (PIH).
Dark marks left after acne or inflammation.
Helpful ingredients include vitamin C,
niacinamide, azelaic acid and exfoliating acids.
Focus on skin tone correction and brightening.
""",

    "uneven_skin_tone": """
Uneven skin tone.
Patchy pigmentation and dull complexion.
Improved by brightening ingredients,
gentle exfoliation and antioxidants.
""",

    "fine_lines": """
Fine lines.
Early signs of aging and collagen reduction.
Helpful ingredients include retinoids,
peptides, antioxidants and hydrating agents.
Focus on collagen stimulation and skin plumping.
""",

    "wrinkles": """
Wrinkles.
Deeper lines caused by collagen and elastin loss.
Improved by retinoids, peptides
and collagen-supporting ingredients.
""",

    "loss_of_firmness": """
Loss of firmness.
Reduced skin elasticity and sagging.
Helpful ingredients include retinoids,
peptides and collagen-stimulating actives.
""",

    "dull_skin": """
Dull skin.
Lack of radiance and uneven texture.
Improved by gentle exfoliation,
brightening ingredients and antioxidants.
""",

    "dark_circles": """
Dark circles.
Under-eye discoloration and pigmentation.
Helpful ingredients include vitamin C,
niacinamide, caffeine and brightening agents.
""",

    "under_eye_bags": """
Under-eye bags.
Puffiness and fluid retention in the under-eye area.
Helpful ingredients include caffeine
and soothing circulation-supporting agents.
"""
}

for row in rows:
    concern_id = row[0]
    template = CONCERN_TEXTS[row[1]]

    text = f"""
        Concern: {row[2]}
        Category: {row[3]}
        {template}
        """

    embedding = model.encode(text).tolist()

    cur.execute(
        "UPDATE concerns SET embedding = %s WHERE id = %s",
        (embedding, concern_id)
    )

conn.commit()
cur.close()
conn.close()
