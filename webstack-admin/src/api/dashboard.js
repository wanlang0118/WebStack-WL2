import request from './request.js'

export const getDashboardTrend = (params) => request.get('/dashboard/trend', { params })
