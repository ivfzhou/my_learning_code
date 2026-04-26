import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    strict: true,
    linkActiveClass: 'router-link-active',
    linkExactActiveClass: 'router-link-exact-active',
    routes: [
        // 动态参数。
        {
            path: '/dynamicParams/:id/:name',
            component: () => import('@/examples/router/dynamic_params/DynamicParams.vue')
        },
        {
            path: '/regular/:id(\\d+)', // 匹配纯数字的动态参数。
            component: () => import('@/examples/router/regular/Regular.vue'),
        },
        {
            path: '/regular/:id([a-zA-Z]+)', // 匹配纯字母的动态参数。
            component: () => import('@/examples/router/regular/Regular2.vue'),
        },
        {
            path: '/regular/:id+', // 匹配动态参数一次或多次定义。还有 * ? 可使用。
            component: () => import('@/examples/router/regular/Regular3.vue'),
        },
        {
            path: '/regular/:id(\\d+)+', // 匹配动态参数是纯数字且定义一次或多次定义。
            component: () => import('@/examples/router/regular/Regular4.vue'),
        },

        // 嵌套路由。
        {
            path: '/nested/',
            component: () => import('@/examples/router/nested/Nested.vue'),
            children: [
                {
                    path: 'profile',
                    component: () => import('@/examples/router/nested/NestedProfile.vue'),
                },
                {
                    path: 'post',
                    component: () => import('@/examples/router/nested/NestedPost.vue'),
                },
                {
                    path: '',
                    component: () => import('@/examples/router/nested/NestedDefault.vue'),
                }
            ]
        },
        {
            // 没有父组件的嵌套路由。
            path: '/nested2/',
            children: [
                {
                    path: 'profile',
                    component: () => import('@/examples/router/nested/NestedProfile.vue'),
                },
                {
                    path: 'post',
                    component: () => import('@/examples/router/nested/NestedPost.vue'),
                },
                {
                    path: '',
                    component: () => import('@/examples/router/nested/NestedDefault.vue'),
                }
            ]
        },

        // 命令路由。
        {
            path: '/namingView/:param',
            name: 'root',
            component: () => import('@/examples/router/naming_view/NamingView.vue'),
        },
        {
            path: '/namingView2',
            components: {
                default: () => import('@/examples/router/naming_view2/NamingViewDefault.vue'),
                left: () => import('@/examples/router/naming_view2/NamingViewLeftSidebar.vue'),
                right: () => import('@/examples/router/naming_view2/NamingViewRightSidebar.vue')
            }
        },
        {
            // 命令路由与嵌套路由结合。
            path: '/namingView3',
            component: () => import('@/examples/router/naming_view3/NamingView3.vue'),
            children: [
                {
                    path: 'nested',
                    components: {
                        default: () => import('@/examples/router/naming_view3/nested/NamingView3Default.vue'),
                        left: () => import('@/examples/router/naming_view3/nested/NamingView3LeftSidebar.vue'),
                        right: () => import('@/examples/router/naming_view3/nested/NamingView3RightSidebar.vue'),
                    }
                }
            ]
        },

        // 重定向。
        {
            path: '/redirect',
            redirect: '/redirecting',
        },
        {
            path: '/redirecting',
            redirect: {name: 'redirect'}
        },
        {
            name: 'redirect',
            path: '/redirect1',
            redirect: to => {
                console.log('to is', to)
                return 'redirect2'
            }
        },
        {
            path: '/redirect2',
            redirect: to => {
                console.log('to is', to)
                return {path: '/redirect3', query: {key: 'value'}}
            }
        },
        {
            path: '/redirect3',
            component: () => import('@/examples/router/redirect/Redirect.vue'),
        },

        // 别名。
        {
            path: '/alias/:param',
            component: () => import('@/examples/router/alias/Alias.vue'),
            alias: ['/alias2/:param', '/alias3/:param']
        },

        // 路由传参。
        {
            path: '/props/:param',
            component: () => import('@/examples/router/props/Props.vue'),
            props: true,
        },
        {
            path: '/props1/:param',
            components: {
                default: () => import('@/examples/router/props/PropsDefault.vue'),
                left: () => import('@/examples/router/props/PropsLeft.vue')
            },
            props: {default: true, left: false}
        },
        {
            path: '/props2',
            component: () => import('@/examples/router/props/Props2.vue'),
            props: {propA: '123', propB: true}
        },
        {
            path: '/props3',
            component: () => import('@/examples/router/props/Props3.vue'),
            props: route => ({query: route.query})
        },
        {
            path: '/props4',
            component: () => import('@/examples/router/props/Props4.vue'),
        },

        // 匹配的路由样式类。
        {
            path: '/hit',
            component: () => import('@/examples/router/hit/Hit.vue'),
        },
        {
            path: '/hit/exact',
            component: () => import('@/examples/router/hit/HitExact.vue'),
        },

        // 兜底组件。
        {
            path: '/:any(.*)+',
            component: () => import('@/examples/router/404.vue')
        }
    ]
})

export default router
