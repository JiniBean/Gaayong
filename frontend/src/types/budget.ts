import type { CategoryItem } from './category'

export interface BudgetItem {
  id: string
  name: string
  des: string
  amt: number | string
  dd: number | string
  ctgId: string
  ctg?: string
}

export interface BudgetListResponse {
  total: number
  list: BudgetItem[]
  categoryList: CategoryItem[]
}

export interface BudgetPayload {
  name: string
  des: string
  amt: string
  dd: string
  ctgId: string
}
