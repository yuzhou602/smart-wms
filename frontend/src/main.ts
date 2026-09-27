import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { Search } from '@element-plus/icons-vue'
import './styles/index.css'
import App from './App.vue'
import router from './router'
import { installPermissionDirective } from './directives/permission'

const app = createApp(App)

app.component('Search', Search)

app.use(createPinia())
app.use(router)
installPermissionDirective(app)

app.mount('#app')
