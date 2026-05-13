# Use the official PostgreSQL image
FROM postgres:16

# Environment variables
ENV POSTGRES_USER=admin
ENV POSTGRES_PASSWORD=securepassword
ENV POSTGRES_DB=task_management

# Expose PostgreSQL default port
EXPOSE 5432

# Optional: Copy initialization SQL scripts
# Any .sql or .sh files placed here will run automatically
COPY schema.sql /docker-entrypoint-schemadb.d/

# Start PostgreSQL
CMD ["postgres"]