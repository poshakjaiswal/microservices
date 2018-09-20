import React from "react"
import { createStore, applyMiddleware, bindActionCreators, compose } from "redux"
import Im, { fromJS, Map } from "immutable"
import { combineReducers } from "redux-immutable"
import deepExtend from "deep-extend"

const idFn = a => a

export default class Store {
    constructor(opts={}){
        deepExtend(this,{
            state: {},//default state of redux 
        },opts);

        this.store = configureStore(idFn, fromJS(this.state),)
    }

    getStore() {
        return this.store
      }

}

function configureStore(rootReducer, initialState){

    let middlwares = [
       // systemThunkMiddleware( getSystem )
      ]

      const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose

      return createStore(rootReducer, initialState, composeEnhancers(
        applyMiddleware( ...middlwares )
      ))

}