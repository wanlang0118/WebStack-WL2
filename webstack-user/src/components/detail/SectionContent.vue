<template>
  <div class="section-content">
    <div v-if="content.contentType === 'paragraph'" class="content-paragraph">
      <p>{{ content.textContent }}</p>
    </div>

    <div v-else-if="content.contentType === 'image'" class="content-image">
      <img :src="imageUrl" :alt="parsedData?.alt || ''" loading="lazy" />
    </div>

    <div v-else-if="content.contentType === 'list_item'" class="content-list">
      <ul>
        <li v-for="(item, idx) in parsedData?.items || []" :key="idx">{{ item }}</li>
      </ul>
    </div>

    <div v-else-if="content.contentType === 'table'" class="content-table">
      <div class="table-wrapper">
        <table>
          <thead v-if="parsedData?.headers?.length">
            <tr>
              <th v-for="(h, i) in parsedData.headers" :key="i">{{ h }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, ri) in parsedData?.rows || []" :key="ri">
              <td v-for="(cell, ci) in row" :key="ci">{{ cell }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, defineProps } from 'vue'

const props = defineProps({
  content: { type: Object, required: true }
})

const parsedData = computed(() => props.content.parsedData || null)

const imageUrl = computed(() => {
  const url = parsedData.value?.url
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) return url
  return `/uploads/${url}`
})
</script>

<style scoped>
.section-content {
  margin-bottom: 16px;
}

.content-paragraph p {
  font-size: 17px;
  line-height: 1.8;
  color: var(--color-text);
  max-width: none;
}

.content-paragraph {
  width: 100%;
  margin-bottom: 8px;
}

.content-image {
  border-radius: var(--radius-md);
  overflow: hidden;
  width: 100%;
  display: block;
}

.content-image img {
  width: 100%;
  height: auto;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border-light); /* 精致描边防止融于白底 */
}

.content-list {
  width: 100%;
}

.content-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.content-list li {
  position: relative;
  padding-left: 1.15em;
  font-size: 16px;
  line-height: 1.8;
  color: var(--color-text);
  margin-bottom: 12px;
}

.content-list li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0.78em;
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: var(--color-text);
  transform: translateY(-50%);
}

.content-table {
  overflow-x: auto;
  max-width: 100%;
}

.table-wrapper table {
  width: 100%;
  border-collapse: collapse;
  font-size: 15px; /* 表格字号提升 */
}

.table-wrapper th,
.table-wrapper td {
  border: 1px solid var(--color-border);
  padding: 12px 16px; /* 表格填充等比拉大 */
  text-align: left;
}

.table-wrapper th {
  background: var(--color-bg);
  font-weight: 600;
}
</style>
