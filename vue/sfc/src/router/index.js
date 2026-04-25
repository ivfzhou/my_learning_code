import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {path: '/user/:id', component: () => import('@/examples/router/dynamic_params/User.vue')}
    ],
})

export default router
