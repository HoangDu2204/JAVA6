// import './assets/main.css'
// import 'bootstrap/dist/css/bootstrap.min.css'
// import 'bootstrap/dist/js/bootstrap.min.js'
// //import 'bootstrap/dist/js/bootstrap.bundle.min.js'
// import 'bootstrap-icons/font/bootstrap-icons.css'
// import { createApp } from 'vue'
// import App from './App.vue'
// import router from './router'

// const app = createApp(App)

// app.use(router)

// app.mount('#app')


import './assets/main.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.min.js'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import 'bootstrap-icons/font/bootstrap-icons.css'

// Import font consistency CSS để đảm bảo font chữ đẹp và nhất quán
import './assets/global-font-fix.css'
import './assets/user-common.css'
import './assets/font-consistency.css'
import './assets/blog-font-fix.css'




import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

//  Thêm Pinia vào
import { createPinia } from 'pinia'

const app = createApp(App)

// Dùng router và pinia
app.use(router)
app.use(createPinia()) // <- dòng này là BẮT BUỘC nếu dùng store

app.mount('#app')
