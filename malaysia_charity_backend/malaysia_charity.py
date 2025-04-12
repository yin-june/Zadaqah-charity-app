import sqlite3
import math

# Initialize database (removed SQL comments)
def init_db():
    conn = sqlite3.connect(':memory:')
    c = conn.cursor()
    
    c.execute('''CREATE TABLE charities (
        id INTEGER PRIMARY KEY,
        name TEXT,
        state TEXT,
        lat REAL,
        lng REAL,
        account TEXT,
        verified INTEGER)''')
    
    # Malaysian charity data
    charities = [
        (1, "MyKasih Foundation", "Kuala Lumpur", 3.1319, 101.6841, "CIMB 8001234567", 1),
        (2, "Pertiwi Soup Kitchen", "Kuala Lumpur", 3.1478, 101.6953, "Maybank 1234567890", 1),
        (3, "Red Crescent Johor", "Johor", 1.4927, 103.7414, "Public Bank 987654321", 1)
    ]
    
    c.executemany('INSERT INTO charities VALUES (?,?,?,?,?,?,?)', charities)
    conn.commit()
    return conn

# Rest of the code remains the same...

# ========================
# GEOLOCATION (No geopy needed)
# ========================
def haversine(lat1, lng1, lat2, lng2):
    """Calculate distance between coordinates in km"""
    R = 6371  # Earth radius in km
    dLat = math.radians(lat2 - lat1)
    dLng = math.radians(lng2 - lng1)
    a = math.sin(dLat/2)**2 + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(dLng/2)**2
    return R * 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))

# ========================
# QR GENERATION (No external packages)
# ========================
def generate_text_qr(charity_id, amount):
    """Generates donation QR data (Malaysia format)"""
    conn = init_db()
    c = conn.cursor()
    c.execute('SELECT name, account FROM charities WHERE id=?', (charity_id,))
    charity = c.fetchone()
    conn.close()
    
    if charity:
        return f"MYSCHARITY|{charity_id}|{charity[0]}|{charity[1]}|RM{amount}"
    return None

def generate_ascii_qr(text):
    """Creates ASCII QR representation"""
    border = '+' + '-'*(len(text)+2) + '+'
    return f"{border}\n| {text} |\n{border}"

# ========================
# CORE FUNCTIONALITY
# ========================
def get_nearby_charities(lat, lng, radius_km=50):
    """Returns verified Malaysian charities within radius"""
    conn = init_db()
    c = conn.cursor()
    results = []
    
    for row in c.execute('SELECT * FROM charities WHERE verified=1'):
        dist = haversine(lat, lng, row[3], row[4])
        if dist <= radius_km:
            results.append({
                'id': row[0],
                'name': row[1],
                'state': row[2],
                'distance_km': round(dist, 1),
                'account': row[5]  # Bank account info
            })
    
    conn.close()
    return sorted(results, key=lambda x: x['distance_km'])

# ========================
# MAIN EXECUTION
# ========================
if __name__ == "__main__":
    # Test with KLCC coordinates
    klcc_lat, klcc_lng = 3.1589, 101.7118
    
    print("Nearby Verified Charities (50km radius from KLCC):")
    for charity in get_nearby_charities(klcc_lat, klcc_lng):
        print(f"\n{charity['name']} ({charity['state']})")
        print(f"Distance: {charity['distance_km']}km")
        print(f"Account: {charity['account']}")
        
        # Generate donation QR
        qr_data = generate_text_qr(charity['id'], 10.00)  # RM10 donation
        print("\nDonation QR:")
        print(generate_ascii_qr(qr_data))