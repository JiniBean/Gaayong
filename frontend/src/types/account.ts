export interface MessageResponse {
  message: string
}

export interface AccountItem {
  id: string
  bank: string
  name: string
  accNum: string
  amt: number | string
}

export interface AccountListResponse {
  total: number
  list: AccountItem[]
}

export interface AccountPayload {
  bank: string
  name: string
  accNum: string
  amt: string
}
