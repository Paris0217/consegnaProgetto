import { nanoid } from 'nanoid'
import { Maybe, Nothing } from "./maybe"

type Message = { receiver: ProntogramID, sender: ProntogramID, text: string }
type MessageEntry = Omit<Message, 'receiver'>
type ProntogramID = string

type Reference = string
type ReferenceEntry = { createdOn: number, value: Reference }

export const initializeDB = () =>
{
  /* Messages database */
  const database: { [receiver: string]: MessageEntry[]} = {}
  /* Temporary references for one-use post requests */
  let references: ReferenceEntry[] = []

  const reference = (): Reference => 
  {
    const random = nanoid()
    references.push({ createdOn: new Date().getTime(), value: random })

    return random
  }

  const create = ({ receiver, sender, text }: Message, reference: Reference): Maybe<number> =>
  {
    const minDate = new Date().getTime() - 86400000
    
    /* Check if the one-use reference is still valid */
    const referenceId = references.findIndex(referenceEntry => referenceEntry.value === reference && referenceEntry.createdOn > minDate)

    if (referenceId < 0) return Nothing
    
    /* Initialize receiver if necessary */
    const receiverDB = database[receiver] = database[receiver] || []
    /* Push the new message  */
    receiverDB.push({ sender, text })
    /* Remove used reference and outdated references */
    references = references.filter(referenceEntry => referenceEntry.value !== reference && referenceEntry.createdOn > minDate)

    return receiverDB.length - 1
  }

  const remove = (receiver: ProntogramID, id: number): Maybe<void> =>
  {
    const receiverDB = database[receiver] = database[receiver] || []

    if (id >= receiverDB.length) return Nothing

    receiverDB.splice(id, 1)
  }

  const read = (receiver: ProntogramID, id: number): Maybe<Message> =>
  {
    const receiverDB = database[receiver] = database[receiver] || []

    if (id >= receiverDB.length) return Nothing

    return { ...receiverDB[id]!, receiver }
  }

  const size = (receiver: string): number =>
  {
    const receiverDB = database[receiver] = database[receiver] || []

    return receiverDB.length
  }

  return {
    reference,
    create,
    remove,
    read,
    size
  }
}

export default initializeDB;

