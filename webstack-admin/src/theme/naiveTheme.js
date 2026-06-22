import { darkTheme } from 'naive-ui'

const commonColors = {
  primaryColor: '#F97316',
  primaryColorHover: '#FB923C',
  primaryColorPressed: '#EA580C',
  primaryColorSuppl: '#FDBA74',
  successColor: '#10B981', /* Softer, cooler green */
  successColorHover: '#34D399',
  successColorPressed: '#059669',
  warningColor: '#F59E0B',
  warningColorHover: '#FBBF24',
  warningColorPressed: '#D97706',
  errorColor: '#EF4444',
  errorColorHover: '#F87171',
  errorColorPressed: '#DC2626',
  infoColor: '#3B82F6',
  infoColorHover: '#60A5FA',
  infoColorPressed: '#2563EB',
  borderRadius: '6px',
  fontFamily: "Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif"
}

export const lightOverrides = {
  common: {
    ...commonColors,
    bodyColor: '#F7F7F8',
    cardColor: '#FFFFFF',
    modalColor: '#FFFFFF',
    popoverColor: '#FFFFFF',
    tableColor: '#FFFFFF',
    inputColor: '#FFFFFF',
    textColorBase: 'rgba(0,0,0,0.88)',
    textColor1: 'rgba(0,0,0,0.88)',
    textColor2: 'rgba(0,0,0,0.65)',
    textColor3: 'rgba(0,0,0,0.45)',
    placeholderColor: 'rgba(0,0,0,0.35)',
    dividerColor: 'rgba(0,0,0,0.09)',
    borderColor: 'rgba(0,0,0,0.12)',
    hoverColor: 'rgba(0,0,0,0.04)',
    tableHeaderColor: 'rgba(0,0,0,0.02)'
  },
  Card: {
    borderRadius: '12px',
    paddingMedium: '24px',
    borderColor: 'rgba(0,0,0,0.09)'
  },
  Button: {
    borderRadiusMedium: '6px',
    borderRadiusSmall: '6px'
  },
  DataTable: {
    borderRadius: '12px',
    borderColor: 'rgba(0,0,0,0.06)',
    thColor: 'rgba(0,0,0,0.02)',
    thColorHover: 'rgba(0,0,0,0.04)',
    tdColorHover: 'rgba(0,0,0,0.03)'
  },
  Input: {
    borderRadius: '6px',
    heightMedium: '36px'
  },
  Modal: {
    borderRadius: '12px',
    paddingMedium: '24px',
    boxShadow: '0 8px 24px rgba(0, 0, 0, 0.08)' /* Reduced heavy shadow on light mode */
  },
  Menu: {
    borderRadius: '6px',
    itemColorActive: 'rgba(249,115,22,0.08)',
    itemColorActiveHover: 'rgba(249,115,22,0.12)',
    itemTextColorActive: '#F97316',
    itemIconColorActive: '#F97316',
    itemTextColorActiveHover: '#F97316',
    itemIconColorActiveHover: '#F97316'
  },
  Tag: {
    borderRadius: '6px'
  }
}

export const darkOverrides = {
  common: {
    ...commonColors,
    bodyColor: '#0E0E11',
    cardColor: '#17171C',
    modalColor: '#1F1F26',
    popoverColor: '#1F1F26',
    tableColor: '#17171C',
    inputColor: '#1F1F26',
    textColorBase: 'rgba(255,255,255,0.92)',
    textColor1: 'rgba(255,255,255,0.92)',
    textColor2: 'rgba(255,255,255,0.72)',
    textColor3: 'rgba(255,255,255,0.5)',
    placeholderColor: 'rgba(255,255,255,0.38)',
    dividerColor: 'rgba(255,255,255,0.08)',
    borderColor: 'rgba(255,255,255,0.12)',
    hoverColor: 'rgba(255,255,255,0.06)',
    tableHeaderColor: 'rgba(255,255,255,0.04)'
  },
  Card: {
    borderRadius: '12px',
    paddingMedium: '24px',
    borderColor: 'rgba(255,255,255,0.08)',
    color: '#17171C'
  },
  Button: {
    borderRadiusMedium: '6px',
    borderRadiusSmall: '6px'
  },
  DataTable: {
    borderRadius: '12px',
    borderColor: 'rgba(255,255,255,0.06)',
    thColor: 'rgba(255,255,255,0.04)',
    thColorHover: 'rgba(255,255,255,0.06)',
    tdColorHover: 'rgba(255,255,255,0.05)',
    color: '#17171C'
  },
  Input: {
    borderRadius: '6px',
    heightMedium: '36px',
    color: '#1F1F26'
  },
  Modal: {
    borderRadius: '12px',
    paddingMedium: '24px',
    color: '#1F1F26',
    boxShadow: '0 8px 24px rgba(0, 0, 0, 0.3)' /* Reduced heavy shadow on dark mode */
  },
  Menu: {
    borderRadius: '6px',
    itemColorActive: 'rgba(249,115,22,0.12)',
    itemColorActiveHover: 'rgba(249,115,22,0.18)',
    itemTextColorActive: '#FB923C',
    itemIconColorActive: '#FB923C',
    itemTextColorActiveHover: '#FB923C',
    itemIconColorActiveHover: '#FB923C',
    color: 'transparent'
  },
  Tag: {
    borderRadius: '6px'
  }
}

export { darkTheme }
