/* AuthState */
export interface SettingsState {
  collapse: boolean
  refresh: boolean
  themeConfig: ThemeConfigProps
}

export interface ThemeConfigProps {
  primary: string
  isDark: boolean
}
