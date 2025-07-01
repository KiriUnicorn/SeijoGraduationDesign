import request from '@/utils/request'

// 获取竞赛列表
export function getCompetitionList(params) {
  return request({
    url: '/competitions/findAll',
    method: 'get',
    params: {
      page: params.page || 1,
      pageSize: params.pageSize || 10,
      keyword: params.keyword || ''
    }
  })
}

// 获取热门竞赛
export function getHotCompetitions(limit = 3) {
  return request({
    url: '/competitions/hot',
    method: 'get',
    params: { limit }
  })
}

// 获取竞赛详情
export function getCompetitionDetail(id) {
  return request({
    url: `/competitions/${id}`,
    method: 'get'
  })
} 