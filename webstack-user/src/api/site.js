import request from './request'

export const getSitePage = (params) => request.get('/site/page', { params })

export const getSiteDetail = (id) => request.get(`/site/${id}`)

export const getSiteSections = (siteId) => request.get('/site-section/list', { params: { siteId } })

export const getSectionContents = (sectionId) =>
  request.get('/site-section-content/list', { params: { sectionId } })

export const recordClick = (siteId) => request.post(`/site/click/${siteId}`)
