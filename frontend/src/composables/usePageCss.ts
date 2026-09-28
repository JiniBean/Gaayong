import { onMounted, onUnmounted } from 'vue'

export function usePageCss(href: string) {
  const id = `page-css-${href.replace(/\W/g, '-')}`

  onMounted(() => {
    if (document.getElementById(id)) return
    const link = document.createElement('link')
    link.id = id
    link.rel = 'stylesheet'
    link.href = href
    document.head.appendChild(link)
  })

  onUnmounted(() => {
    document.getElementById(id)?.remove()
  })
}
