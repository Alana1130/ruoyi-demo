import request from '@/utils/request'

export function listTeams() {
  return request({
    url: '/travel/aa/teams',
    method: 'get'
  })
}

export function getTeam(id) {
  return request({
    url: '/travel/aa/teams/' + id,
    method: 'get'
  })
}

export function listBills() {
  return request({
    url: '/travel/aa/bills',
    method: 'get'
  })
}

export function getBill(id) {
  return request({
    url: '/travel/aa/bills/' + id,
    method: 'get'
  })
}

export function listExpenses() {
  return request({
    url: '/travel/aa/expenses',
    method: 'get'
  })
}

export function calculateBill(id) {
  return request({
    url: '/travel/aa/bills/' + id + '/calculate',
    method: 'post'
  })
}

export function allocateBill(id) {
  return request({
    url: '/travel/aa/bills/' + id + '/allocate',
    method: 'post'
  })
}

export function getTeamStatistics(id) {
  return request({
    url: '/travel/aa/teams/' + id + '/statistics',
    method: 'get'
  })
}

export function exportBill(id) {
  return request({
    url: '/travel/aa/bills/' + id + '/export',
    method: 'get'
  })
}