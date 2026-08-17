import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
const store = createPinia();
// piniaPersist(持久化)
store.use(piniaPluginPersistedstate);
export { store };
