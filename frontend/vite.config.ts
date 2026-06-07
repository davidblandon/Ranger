import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// During local dev, proxy /api/* to the correct backend service.
// RH-owned paths go to rh-service (8081); everything else goes to production-service (8082).
const API_TARGET    = process.env.VITE_API_PROXY    ?? "http://localhost:8082";
const RH_API_TARGET = process.env.VITE_RH_API_PROXY ?? "http://localhost:8081";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      // RH service paths — must come before the catch-all /api rule
      "^/api/(auth|employees|payrolls|shifts|admins)(/|$)": {
        target: RH_API_TARGET,
        changeOrigin: true,
      },
      "/api": {
        target: API_TARGET,
        changeOrigin: true,
      },
    },
  },
});
