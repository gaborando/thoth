export interface Form{
  type: 'template' | 'renderer',
  id: string,
  name: string,
  fields: string[],
  token: string
}
