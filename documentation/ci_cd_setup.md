# Notes

## Overview
We will be using GitHub Actions for CI/CD. The workflows are located in `.github/workflows/`.

## Branching Strategy
- `feature_*` -> our separate develop on diff. features
- `dev` -> CI would run here: build + test + liniting??
- `main` -> CD runs here

## Workflows

| File | Trigger | What it does |
|------|---------|--------------|
| `backend.yml` | push/PR to `dev`, push to `main` | builds spring boot, deploys to azure app service |
| `frontend.yml` | push/PR to `dev`, push to `main` | builds flutter web, deploys to azure static web apps |
| `functions-notifications.yml` | push/PR to `dev`, push to `main` | builds & deploys notifications azure function |
| `functions-jobs.yml` | push/PR to `dev`, push to `main` | deploys jobs azure function |

## Note: GitHub Secrets Required
These secrets we would later added to the repo's actions:

| Secret Name | Used In | 
|-------------|---------|
| `AZURE_WEBAPP_PUBLISH_PROFILE` | `backend.yml` | 
| `AZURE_STATIC_WEB_APPS_API_TOKEN` | `frontend.yml` |
| `AZURE_FUNCTIONAPP_NOTIFICATIONS_PUBLISH_PROFILE` | `functions-notifications.yml` |
| `AZURE_FUNCTIONAPP_JOBS_PUBLISH_PROFILE` | `functions-jobs.yml` |

## Once we have set up azure we need to 
1. We would need to update the app names and the token secrets

