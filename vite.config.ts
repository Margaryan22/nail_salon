// vite.config.ts

import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { resolve } from 'path'; // Импортируем 'resolve' для создания абсолютного пути

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      // Это принудительно устанавливает, что 'react' всегда указывает на одну и ту же папку.
      react: resolve(__dirname, 'node_modules/react'),
      'react-dom': resolve(__dirname, 'node_modules/react-dom'),
    },
  },
});
