SELECT 'CREATE DATABASE recommendation_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'recommendation_db')\gexec