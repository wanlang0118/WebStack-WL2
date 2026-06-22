<template>
  <footer class="footer">
    <div class="footer-inner">
      <p class="footer-text">
        WebStack-WL 网址导航 · 收录优质网站与工具
      </p>
      <p class="footer-copy">&copy; {{ year }} WebStack-WL</p>
    </div>
  </footer>
</template>

<script setup>
const year = new Date().getFullYear()
</script>

<style scoped>
.footer {
  padding: 32px 24px;
  text-align: center;
  border-top: 1px solid var(--color-border-light);
  background: var(--color-bg-sidebar);
}

.footer-inner {
  max-width: 800px;
  margin: 0 auto;
}

.footer-text {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: 4px;
}

.footer-copy {
  font-size: 12px;
  color: var(--color-text-light);
}
</style>
