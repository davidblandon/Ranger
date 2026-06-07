import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// During local dev, proxy /api to the Docker-mapped production-service (8082)
// so the frontend never hard-codes a backend URL and CORS is a non-issue.
const API_TARGET = process.env.VITE_API_PROXY ?? "http://localhost:8082";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      "/api": {
        target: API_TARGET,
        changeOrigin: true,
      },
    },
  },
});
