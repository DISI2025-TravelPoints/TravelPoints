import random
import uuid
from datetime import datetime, timedelta
from faker import Faker
from sqlalchemy import create_engine, Column, String, DateTime, ForeignKey
from sqlalchemy.orm import sessionmaker, relationship
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.dialects.mysql import BINARY 

DATABASE_URL = "mysql+mysqlconnector://admin:travelpoints@travelpoints.crk4ggmecmeg.eu-central-1.rds.amazonaws.com:3306/attraction_db"

Base = declarative_base()

class Attraction(Base):
    __tablename__ = 'attraction'
    
    id = Column(BINARY(16), primary_key=True) 
    name = Column(String(255))
    description = Column(String(255))
    entryFee = Column(String(255), name='entry_fee')
    audioFilePath = Column(String(255), name='audio_file_path')
    lastUpdate = Column(DateTime, name='last_update')

    
class Visit(Base):
    __tablename__ = 'visit'

    id = Column(BINARY(16), primary_key=True,  default=lambda: uuid.uuid4().bytes)  
    timestamp = Column(DateTime)
    attraction_id = Column(String(36), ForeignKey('attraction.id'))

    attraction = relationship("Attraction")
    
engine = create_engine(DATABASE_URL, echo=True)
Session = sessionmaker(bind=engine)
session = Session()

fake = Faker()

attractions = session.query(Attraction).all()

def generate_visits(num_visits=100):
    for _ in range(num_visits):
        attraction = random.choice(attractions)
        
        random_days_ago = random.randint(0,30)
        
        timestamp = datetime.now() - timedelta(days=random_days_ago)
        
        visit = Visit(
            timestamp=timestamp,
            attraction_id = attraction.id
        )
        
        session.add(visit)
    session.commit()

generate_visits(100)

print("Mock visit data generated successfully.")