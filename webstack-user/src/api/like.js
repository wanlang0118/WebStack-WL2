import request from './request'

export const getLikeStatus = (siteId) => request.get(`/site/like/status/${siteId}`)

export const likeSite = (siteId) => request.post(`/site/like/${siteId}`)

export const unlikeSite = (siteId) => request.delete(`/site/like/${siteId}`)
