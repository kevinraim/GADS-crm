/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        fondo: '#EEF1F4',
        superficie: '#FFFFFF',
        texto: '#16202B',
        linea: '#D3DAE1',
        acento: '#0E5C8A',
        ganada: '#15803D',
        perdida: '#C2410C',
      },
      fontFamily: {
        sans: ['"IBM Plex Sans"', 'ui-sans-serif', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
