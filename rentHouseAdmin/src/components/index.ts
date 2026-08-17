import type { App, Component } from 'vue'

import { SvgIcon } from './SvgIcon'
import { SearchForm } from './SearchForm'
import { ProTable } from './ProTable'
import { SwitchDark } from './SwitchDark'
import { IconifyIcon } from './IconifyIcon'
const Components: {
  [propName: string]: Component
} = {
  SvgIcon,
  SearchForm,
  ProTable,
  SwitchDark,
  IconifyIcon,
}

export default {
  install: (app: App) => {
    Object.keys(Components).forEach((key) => {
      app.component(key, Components[key])
    })
  },
}
