export default function setPageTitle(routerTitle?: string): void {
  window.document.title = routerTitle || import.meta.env.VITE_API_TITLE;
}
