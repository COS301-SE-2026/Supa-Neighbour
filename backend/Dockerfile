FROM postgres:16

# Environment variables

# Expose PostgreSQL default port
EXPOSE 5432

# Optional: Copy initialization SQL scripts
# Any .sql or .sh files placed here will run automatically
COPY schema.sql /docker-entrypoint-initdb.d/

# Start PostgreSQL
CMD ["postgres"]