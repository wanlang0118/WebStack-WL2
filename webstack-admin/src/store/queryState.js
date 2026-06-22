import { defineStore } from 'pinia'

export const useSiteQueryStore = defineStore('siteQuery', {
  state: () => ({
    title: '',
    categoryId: null,
    visible: null,
    isRecommended: null,
    page: 1,
    pageSize: 10
  }),
  persist: true
})

export const useSiteSectionQueryStore = defineStore('siteSectionQuery', {
  state: () => ({
    title: '',
    categoryId: null,
    page: 1,
    pageSize: 12
  }),
  persist: true
})

export const useSiteSectionDetailStore = defineStore('siteSectionDetail', {
  state: () => ({
    siteId: null,
    activeSectionId: null,
    activeSectionTitle: ''
  }),
  persist: true
})
