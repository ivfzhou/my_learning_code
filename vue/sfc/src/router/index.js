import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/user/:id/:name',
            component: () => import('@/examples/router/dynamic_params/User.vue')
        },
        {
            path: '/order/:orderId(\\d+)', // 匹配纯数字的动态参数。
            component: () => import('@/examples/router/regular/Order.vue'),
        },
        {
            path: '/order/:orderId([a-zA-Z]+)', // 匹配纯字母的动态参数。
            component: () => import('@/examples/router/regular/Order2.vue'),
        },
        {
            path: '/order/:orderId+', // 匹配动态参数一次或多次定义。还有 * ? 可使用。
            component: () => import('@/examples/router/regular/Order3.vue'),
        },
        {
            path: '/order/:orderId(\\d+)+', // 匹配动态参数是纯数字且定义一次或多次定义。
            component: () => import('@/examples/router/regular/Order4.vue'),
        },
        {
            path: '/user2',
            redirect: '/user2/',
        },
        {
            // 嵌套路由。
            path: '/user2/',
            component: () => import('@/examples/router/nested/User.vue'),
            children: [
                {
                    path: 'profile',
                    component: () => import('@/examples/router/nested/Profile.vue'),
                },
                {
                    path: 'post',
                    component: () => import('@/examples/router/nested/Post.vue'),
                },
                {
                    path: '',
                    component: () => import('@/examples/router/nested/Default.vue'),
                }
            ]
        },
        {
            // 没有父组件的嵌套路由。
            path: '/user3/',
            children: [
                {
                    path: 'profile',
                    component: () => import('@/examples/router/nested/Profile.vue'),
                },
                {
                    path: 'post',
                    component: () => import('@/examples/router/nested/Post.vue'),
                },
                {
                    path: '',
                    component: () => import('@/examples/router/nested/Default.vue'),
                }
            ]
        },
        {
            // 不显示父组件。
            path: '/user4/',
            component: () => import('@/examples/router/nested/User.vue'),
            children: [
                {
                    path: '/profile',
                    name: 'Profile',
                    component: () => import('@/examples/router/nested/Profile.vue'),
                },
            ]
        }
    ],
    strict: true,
})

export default router
