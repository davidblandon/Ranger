# Frontend — Ranger

React application for the Ranger production-management system. It consumes the
Spring Boot microservices in `../services/` (production-service: batch, material,
order, partner, product; rh-service: HR).

## Always Do First
- **Invoke the `frontend-design` skill** before writing any frontend code, every session, no exceptions.

## Goal
Build a **modern, professional, and clean** interface — clear hierarchy, generous
whitespace, consistent spacing, and restrained color. Avoid generic AI-template
aesthetics.

## Stack
- React + Vite + TypeScript
- React Router for navigation
- Data fetching via a typed API client (one module per service domain)

## Conventions
- Components in `src/components/`, pages in `src/pages/`, API clients in `src/api/`.
- One file per component; PascalCase for components, camelCase for functions/vars.
- Keep components small and focused; lift shared logic into hooks (`src/hooks/`).
- Centralize design tokens (colors, spacing, typography) — no hard-coded magic values.
- Handle loading, empty, and error states for every data-driven view.

## Don't
- Don't hard-code API URLs — read the base URL from env config.
- Don't add a dependency when a few lines of code will do.
- Don't ship inconsistent spacing, fonts, or colors.
