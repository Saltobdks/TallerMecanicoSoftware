import {defineConfig} from 'vite';
import {resolve} from 'node:path';

/** Construye las dos entradas públicas: autenticación y alta de clientes. */
export default defineConfig({
  build: { rollupOptions: { input: { login: resolve(import.meta.dirname, 'index.html'), dashboard: resolve(import.meta.dirname, 'dashboard.html'), customers: resolve(import.meta.dirname, 'customer.html'), workshops: resolve(import.meta.dirname, 'workshop.html') } } }
});
