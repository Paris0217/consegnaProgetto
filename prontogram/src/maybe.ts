export type Maybe<T> = Just<T> | Nothing

type Just<T> = T

export const Nothing = { __nothing: true }
type Nothing = typeof Nothing


export const isEmpty = <T>(justOrNothing: Maybe<T>): justOrNothing is Nothing => justOrNothing === Nothing
export const isNothing = isEmpty
export const isJust = <T>(justOrNothing: Maybe<T>): justOrNothing is T => justOrNothing !== Nothing
