export interface CardItem {
  id: string
  issuer: string
  name: string
  dd: number | string
}

export interface CardListResponse {
  list: CardItem[]
}

export interface CardPayload {
  issuer: string
  name: string
  dd: string
}
