import { defineConfig } from 'vitest/config'
import { resolve } from 'node:path'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(import.meta.dirname, 'src'),
    },
  },
  test: {
    environment: 'jsdom',
    clearMocks: true,
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json-summary'],
      include: [
        'src/stores/user.ts',
        'src/directives/permission.ts',
        'src/router/index.ts',
      ],
      thresholds: {
        'src/stores/user.ts': { 100: true },
        'src/directives/permission.ts': { 100: true },
        'src/router/index.ts': {
          statements: 50,
          branches: 90,
          functions: 20,
          lines: 50,
        },
      },
    },
  },
})
