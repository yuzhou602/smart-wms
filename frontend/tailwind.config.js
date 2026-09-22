/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#2563EB',
        'primary-hover': '#1D4ED8',
        success: '#16A34A',
        warning: '#D97706',
        danger: '#DC2626',
      },
    },
  },
  plugins: [],
}
