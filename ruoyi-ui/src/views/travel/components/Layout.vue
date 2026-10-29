<template>
  <div class="travel-layout">
    <div class="travel-header">
      <div class="header-content">
        <h1 class="header-title">星旅管理</h1>
        <p class="header-subtitle">AA记账管理系统</p>
      </div>
    </div>
    <div class="travel-content">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TravelLayout'
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.travel-layout {
  min-height: 100vh;
  background: #f5f7fa;

  .travel-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    padding: 20px 30px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);

    .header-content {
      display: flex;
      align-items: center;
      gap: 20px;
    }

    .header-title {
      font-size: 24px;
      font-weight: 600;
      margin: 0;
    }

    .header-subtitle {
      font-size: 14px;
      opacity: 0.9;
      margin: 0;
    }
  }

  .travel-content {
    padding: 20px;
  }
}
</style>