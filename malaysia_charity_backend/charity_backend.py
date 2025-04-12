import sqlite3
import math

# Initialize database with Malaysian charities
def init_db():
    conn = sqlite3.connect(':memory:')
    c = conn.cursor()
    
    c.execute('''CREATE TABLE charities (
        id INTEGER PRIMARY KEY,
        name TEXT,
        state TEXT,
        lat REAL,
        lng REAL,
        verified INTEGER)''')
    
    # Real Malaysian charity data
    charities = [
        (1, "MyKasih Foundation", "Kuala Lumpur", 3.1319, 101.6841, 1),
        (2, "Pertiwi Soup Kitchen", "Kuala Lumpur", 3.1478, 101.6953, 1),
        (3, "Red Crescent Johor", "Johor", 1.4927, 103.7414, 1),
        (4, "Penang Animal Shelter", "Penang", 5.4145, 100.3292, 1)
    ]
    
    c.executemany('INSERT INTO charities VALUES (?,?,?,?,?,?)', charities)
    conn.commit()
    return conn

# Fixed Haversine formula (calculates distance in km)
def haversine(lat1, lng1, lat2, lng2):
    R = 6371  # Earth radius in km
    dLat = math.radians(lat2 - lat1)
    dLng = math.radians(lng2 - lng1)
    a = math.sin(dLat/2) ** 2 + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(dLng/2) ** 2
    return R * 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

# Get nearby verified charities
def get_nearby_charities(lat, lng, radius_km=50):
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
                'distance_km': round(dist, 1)
            })
    
    conn.close()
    return sorted(results, key=lambda x: x['distance_km'])

# Main execution
if __name__ == "__main__":
    print("Nearby Verified Charities in Malaysia:")
    klcc_lat, klcc_lng = 3.1589, 101.7118  # KLCC coordinates
    
    for charity in get_nearby_charities(klcc_lat, klcc_lng):
        print(f"{charity['name']} ({charity['state']}) - {charity['distance_km']}km")