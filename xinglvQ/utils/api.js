/**
 * 旅游接口 API
 */
import request from './request.js'

export default {
  auth: {
    login(body) {
      return request.post('/travel/miniapp/auth/login', body || {}).then(res => {
        console.log('API登录响应:', res)
        if (res && res.token) request.setToken(res.token)
        return res
      })
    },
    register(body) {
      return request.post('/travel/miniapp/auth/register', body || {}).then(res => {
        console.log('API注册响应:', res)
        if (res && res.token) request.setToken(res.token)
        return res
      })
    },
    logout() {
      return request.post('/travel/miniapp/auth/logout', {}).then(() => request.clearToken())
    }
  },
  schools: {
    list() { return request.get('/travel/miniapp/schools') },
    verify(schoolId) { return request.post('/travel/miniapp/user/school-verify', { schoolId }) },
    mySchool() { return request.get('/travel/miniapp/user/my-school') }
  },
  schoolCircle: {
    posts(schoolId, page, pageSize) {
      return request.get('/travel/miniapp/school-circle/posts', { schoolId, page: page || 0, pageSize: pageSize || 20 })
    },
    post(body) { return request.post('/travel/miniapp/school-circle/post', body) }
  },
  banners: {
    list() { return request.get('/travel/miniapp/banners') }
  },
  destinations: {
    list() {
      return request.get('/travel/miniapp/destinations')
    },
    get(id) {
      return request.get('/travel/miniapp/destinations/' + id)
    }
  },
  routes: {
    byDestination(id) {
      return request.get('/travel/miniapp/routes/by-destination/' + id)
    },
    get(id) {
      return request.get('/travel/miniapp/routes/' + id)
    }
  },
  budget: {
    plan(params) {
      return request.get('/travel/miniapp/budget/plan', params)
    }
  },
  checkin: {
    light(destinationId) {
      return request.post('/travel/miniapp/checkin/light', { destinationId })
    },
    my() {
      return request.get('/travel/miniapp/checkin/my')
    }
  },
  taxi: {
    list(destinationId) { return request.get('/travel/miniapp/taxi', { destinationId }) }
  },
  tickets: {
    list(destinationId) { return request.get('/travel/miniapp/tickets', { destinationId }) }
  },
  user: {
    me() { return request.get('/travel/miniapp/user/me') },
    updateProfile(data) { return request.put('/travel/miniapp/user/profile', data) },
    changePassword(data) { return request.put('/travel/miniapp/user/change-password', data) },
    bindPhone(data) { return request.post('/travel/miniapp/user/bind-phone', data) }
  },
  experiences: {
    submit(data) { return request.post('/travel/miniapp/experiences', data) },
    list(targetType, targetId) { return request.get('/travel/miniapp/experiences/list', { targetType, targetId }) },
    get(id) { return request.get('/travel/miniapp/experiences/' + id) }
  },
  addresses: {
    list() { return request.get('/travel/miniapp/addresses') },
    add(data) { return request.post('/travel/miniapp/addresses', data) },
    update(id, data) { return request.put('/travel/miniapp/addresses/' + id, data) },
    delete(id) { return request.delete('/travel/miniapp/addresses/' + id) },
    setDefault(id) { return request.put('/travel/miniapp/addresses/' + id + '/default', {}) }
  },
  rewards: {
    items(type) {
      return request.get('/travel/miniapp/rewards/items', type ? { type } : {})
    },
    get(id) {
      return request.get('/travel/miniapp/rewards/items/' + id)
    },
    exchange(rewardItemId) {
      return request.post('/travel/miniapp/rewards/exchange', { rewardItemId })
    },
    purchase(rewardItemId, receiver) {
      const body = { rewardItemId }
      if (receiver) {
        if (receiver.receiverName) body.receiverName = receiver.receiverName
        if (receiver.receiverPhone) body.receiverPhone = receiver.receiverPhone
        if (receiver.receiverAddress) body.receiverAddress = receiver.receiverAddress
      }
      return request.post('/travel/miniapp/rewards/purchase', body)
    },
    myExchanges() {
      return request.get('/travel/miniapp/rewards/my-exchanges')
    },
    myRecords() {
      return request.get('/travel/miniapp/rewards/my-records')
    }
  },
  opportunities: {
    list(params) {
      return request.get('/travel/miniapp/opportunities', params || {})
    },
    get(id) {
      return request.get('/travel/miniapp/opportunities/' + id)
    }
  },
  chat: {
    list(topicType, topicId) {
      return request.get('/travel/miniapp/chat/messages', { topicType, topicId })
    },
    post(data) {
      return request.post('/travel/miniapp/chat/messages', data)
    }
  },
  notifications: {
    list() { return request.get('/travel/miniapp/notifications') },
    unreadCount() { return request.get('/travel/miniapp/notifications/unread-count') },
    markRead(id) { return request.put('/travel/miniapp/notifications/' + id + '/read', {}) },
    markAllRead() { return request.put('/travel/miniapp/notifications/read-all', {}) },
    seedDemo() { return request.post('/travel/miniapp/notifications/seed-demo', {}) }
  },
  search: {
    keyword(k) { return request.get('/travel/miniapp/search', { keyword: k || '' }) }
  },
  companions: {
    list(params) { return request.get('/travel/miniapp/companions', params || {}) },
    get(id) { return request.get('/travel/miniapp/companions/' + id) },
    create(data) { return request.post('/travel/miniapp/companions', data) },
    update(id, data) { return request.put('/travel/miniapp/companions/' + id, data) },
    delete(id) { return request.delete('/travel/miniapp/companions/' + id) }
  },
  guides: {
    list(params) { return request.get('/travel/miniapp/guides', params || {}) },
    get(id) { return request.get('/travel/miniapp/experiences/' + id) }
  },
  smallWorld: {
    overview() { return request.get('/travel/miniapp/small-world/overview') },
    seedDemo() { return request.post('/travel/miniapp/small-world/seed-demo') },
    pets() { return request.get('/travel/miniapp/small-world/pets') },
    licenses() { return request.get('/travel/miniapp/small-world/licenses') },
    feedPet(userPetId) { return request.post('/travel/miniapp/small-world/feed-pet', { userPetId }) },
    playPet(userPetId) { return request.post('/travel/miniapp/small-world/play-pet', { userPetId }) },
    updatePetNickname(userPetId, nickname) { return request.put('/travel/miniapp/small-world/pet-nickname', { userPetId, nickname }) },
    tradeListings(params) { return request.get('/travel/miniapp/small-world/trade-listings', params || {}) },
    myListings() { return request.get('/travel/miniapp/small-world/my-listings') },
    createListing(itemType, itemId, starsAmount) { return request.post('/travel/miniapp/small-world/list', { itemType, itemId, starsAmount }) },
    buyListing(tradeId) { return request.post('/travel/miniapp/small-world/buy', { tradeId }) },
    cancelListing(tradeId) { return request.post('/travel/miniapp/small-world/cancel-listing', { tradeId }) }
  },
  countdowns: {
    list() { return request.get('/travel/miniapp/countdowns') },
    add(data) { return request.post('/travel/miniapp/countdowns', data) },
    update(id, data) { return request.put('/travel/miniapp/countdowns/' + id, data) },
    delete(id) { return request.delete('/travel/miniapp/countdowns/' + id) }
  },
  plans: {
    list() { return request.get('/travel/miniapp/plans') },
    get(id) { return request.get('/travel/miniapp/plans/' + id) },
    add(data) { return request.post('/travel/miniapp/plans', data) },
    update(id, data) { return request.put('/travel/miniapp/plans/' + id, data) },
    delete(id) { return request.delete('/travel/miniapp/plans/' + id) }
  },
  idle: {
    list(params) { return request.get('/travel/miniapp/idle', params || {}) },
    my() { return request.get('/travel/miniapp/idle/my') },
    get(id) { return request.get('/travel/miniapp/idle/' + id) },
    publish(data) { return request.post('/travel/miniapp/idle', data) },
    update(id, data) { return request.put('/travel/miniapp/idle/' + id, data) },
    delete(id) { return request.delete('/travel/miniapp/idle/' + id) },
    buy(idleId) { return request.post('/travel/miniapp/idle/buy', { idleId }) }
  },
  interact: {
    likeToggle(targetType, targetId) { return request.post('/travel/miniapp/interact/like', { targetType, targetId }) },
    likeInfo(targetType, targetId) { return request.get('/travel/miniapp/interact/like-info', { targetType, targetId }) },
    commentList(targetType, targetId) { return request.get('/travel/miniapp/interact/comments', { targetType, targetId }) },
    commentAdd(targetType, targetId, content, parentId) { return request.post('/travel/miniapp/interact/comment', { targetType, targetId, content, parentId: parentId || null }) },
    wantGoToggle(destinationId) { return request.post('/travel/miniapp/interact/want-go', { destinationId }) },
    destinationStats(destinationId) { return request.get('/travel/miniapp/interact/destination-stats', { destinationId }) }
  },
  aa: {
    // 队伍管理
    createTeam(teamName, travelName) { return request.post('/travel/miniapp/aa/team/create', { teamName, travelName }) },
    joinTeam(joinCode) { return request.post('/travel/miniapp/aa/team/join', { joinCode }) },
    myTeams() { return request.get('/travel/miniapp/aa/teams') },
    getTeamDetail(teamId) { return request.get('/travel/miniapp/aa/team/' + teamId) },
    getTeamMembers(teamId) { return request.get('/travel/miniapp/aa/team/' + teamId + '/members') },
    updateTeamStatus(teamId, status) { return request.put('/travel/miniapp/aa/team/' + teamId + '/status', { status }) },
    leaveTeam(teamId) { return request.post('/travel/miniapp/aa/team/' + teamId + '/leave', {}) },
    
    // 账单管理
    createBill(teamId, billName) { return request.post('/travel/miniapp/aa/bill/create', { teamId, billName }) },
    getTeamBills(teamId) { return request.get('/travel/miniapp/aa/team/' + teamId + '/bills') },
    getBillDetail(billId) { return request.get('/travel/miniapp/aa/bill/' + billId) },
    
    // 消费管理
    addExpense(billId, teamId, expenseName, amount, category, description, paymentMethod) {
      return request.post('/travel/miniapp/aa/expense/add', { billId, teamId, expenseName, amount, category, description, paymentMethod })
    },
    getBillExpenses(billId) { return request.get('/travel/miniapp/aa/bill/' + billId + '/expenses') },
    getTeamExpenses(teamId) { return request.get('/travel/miniapp/aa/team/' + teamId + '/expenses') },
    
    // 账单结算
    calculateBill(billId) { return request.get('/travel/miniapp/aa/bill/' + billId + '/calculate') },
    generateBillAllocation(billId) { return request.post('/travel/miniapp/aa/bill/' + billId + '/allocate', {}) },
    payBillAllocation(allocationId, amount) { return request.post('/travel/miniapp/aa/allocation/' + allocationId + '/pay', { amount }) },
    
    // 统计和导出
    getTeamStatistics(teamId) { return request.get('/travel/miniapp/aa/team/' + teamId + '/statistics') },
    exportBill(billId) { return request.post('/travel/miniapp/aa/bill/' + billId + '/export', {}) }
  }
}
