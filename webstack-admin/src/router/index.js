import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'
import AdminLayout from '../views/layout/AdminLayout.vue'
import DashboardView from '../views/dashboard/DashboardView.vue'
import CategoryView from '../views/category/CategoryView.vue'
import SiteView from '../views/site/SiteView.vue'
import TagView from '../views/tag/TagView.vue'
import SiteSectionView from '../views/siteSection/SiteSectionView.vue'
import SiteSectionDetailView from '../views/siteSection/SiteSectionDetailView.vue'
import LoginView from '../views/login/LoginView.vue'
import UserView from '../views/user/UserView.vue'
import ClickLogView from '../views/siteClickLog/ClickLogView.vue'
import LikeLogView from '../views/siteLike/LikeLogView.vue'
import CommentView from '../views/siteComment/CommentView.vue'
import NoticeView from '../views/sysNotice/NoticeView.vue'
import SiteImportView from '../views/site/SiteImportView.vue'

const routes = [
  {
    path: '/login',
    component: LoginView,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: AdminLayout,
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', component: DashboardView, meta: { requiresAuth: true } },
      { path: 'category', component: CategoryView, meta: { requiresAuth: true } },
      { path: 'site', component: SiteView, meta: { requiresAuth: true } },
      { path: 'tag', component: TagView, meta: { requiresAuth: true } },
      { path: 'site-section', component: SiteSectionView, meta: { requiresAuth: true } },
      { path: 'site-section/detail/:siteId', component: SiteSectionDetailView, meta: { requiresAuth: true } },
      { path: 'user', component: UserView, meta: { requiresAuth: true } },
      { path: 'click-log', component: ClickLogView, meta: { requiresAuth: true } },
      { path: 'like-log', component: LikeLogView, meta: { requiresAuth: true } },
      { path: 'comment', component: CommentView, meta: { requiresAuth: true } },
      { path: 'notice', component: NoticeView, meta: { requiresAuth: true } },
      { path: 'site-import', component: SiteImportView, meta: { requiresAuth: true } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.path === '/login' && userStore.token) {
    next('/')
  } else {
    next()
  }
})

export default router
