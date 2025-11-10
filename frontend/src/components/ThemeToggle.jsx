import React from 'react';
import { useTheme } from '../hooks/useTheme';

export const ThemeToggle = () => {
  const { isDark, toggleTheme } = useTheme();

  return (
    <button
      className="btn btn-outline-secondary"
      onClick={toggleTheme}
      title="Toggle dark/light mode"
    >
      {isDark ? '☀️ Light' : '🌙 Dark'}
    </button>
  );
};