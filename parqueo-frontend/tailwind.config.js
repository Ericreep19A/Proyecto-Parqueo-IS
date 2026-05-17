/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['"Plus Jakarta Sans"', 'system-ui', 'sans-serif'],
        display: ['"Instrument Serif"', 'Georgia', 'serif'],
        mono: ['"JetBrains Mono"', 'monospace'],
      },
      colors: {
        // Paleta institucional: navy profundo + dorado academico
        ink: {
          50:  '#F8FAFC',
          100: '#F1F5F9',
          200: '#E2E8F0',
          300: '#CBD5E1',
          400: '#94A3B8',
          500: '#64748B',
          600: '#475569',
          700: '#334155',
          800: '#1E293B',
          900: '#0F172A',
          950: '#020617',
        },
        gold: {
          50:  '#FEFCE8',
          100: '#FEF9C3',
          200: '#FEF08A',
          300: '#FDE047',
          400: '#FACC15',
          500: '#EAB308',
          600: '#CA8A04',
          700: '#A16207',
          800: '#854D0E',
          900: '#713F12',
        },
        emerald: {
          500: '#10B981',
          600: '#059669',
          700: '#047857',
        },
        rose: {
          500: '#F43F5E',
          600: '#E11D48',
          700: '#BE123C',
        },
      },
      backgroundImage: {
        'mesh-dark':
          'radial-gradient(at 27% 37%, hsla(215, 98%, 61%, 0.10) 0px, transparent 50%), ' +
          'radial-gradient(at 97% 21%, hsla(48, 80%, 60%, 0.12) 0px, transparent 50%), ' +
          'radial-gradient(at 52% 99%, hsla(220, 70%, 30%, 0.15) 0px, transparent 50%), ' +
          'radial-gradient(at 10% 29%, hsla(256, 96%, 67%, 0.08) 0px, transparent 50%)',
        'noise':
          "url(\"data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)' opacity='0.25'/%3E%3C/svg%3E\")",
      },
      boxShadow: {
        'soft':   '0 1px 2px rgba(15,23,42,0.04), 0 8px 24px rgba(15,23,42,0.06)',
        'lifted': '0 4px 8px rgba(15,23,42,0.06), 0 20px 40px rgba(15,23,42,0.10)',
        'inset-line': 'inset 0 0 0 1px rgba(255,255,255,0.06)',
      },
      animation: {
        'fade-up':  'fadeUp 0.5s cubic-bezier(0.22, 1, 0.36, 1) both',
        'fade-in':  'fadeIn 0.4s ease-out both',
        'pulse-ring': 'pulseRing 2.4s ease-out infinite',
        'shimmer':  'shimmer 2s linear infinite',
      },
      keyframes: {
        fadeUp:    { from: { opacity: 0, transform: 'translateY(12px)' }, to: { opacity: 1, transform: 'translateY(0)' } },
        fadeIn:    { from: { opacity: 0 }, to: { opacity: 1 } },
        pulseRing: { '0%': { transform: 'scale(1)', opacity: 0.7 }, '100%': { transform: 'scale(1.6)', opacity: 0 } },
        shimmer:   { '0%': { backgroundPosition: '-200% 0' }, '100%': { backgroundPosition: '200% 0' } },
      },
    },
  },
  plugins: [],
};
