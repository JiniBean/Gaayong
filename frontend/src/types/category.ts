export interface CodeItem {
  detailCd: string
  detailNm: string
  groupCd?: string
}

export interface CategoryItem {
  id: string
  name: string
  des: string
  ctgCd: string
  userId?: string
}

export interface CategoryListResponse {
  codeList: CodeItem[]
  list: CategoryItem[]
  filter: string | null
}

export interface CategoryPayload {
  name: string
  des: string
  ctgCd: string
}

export interface MessageResponse {
  message: string
}
