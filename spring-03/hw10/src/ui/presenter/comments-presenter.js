import React from 'react'
import {Header} from '../service/header-service.js'
import ReactDOM from "react-dom"
import CommentCreatePresenter from './comment-create-presenter.js'
import App from './books-presenter.js'


export default class CommentsPresenter extends React.Component {
    constructor(props) {
        super(props)
        this.state = {bookId: props.bookId, comments: [], isFirstLoad: true}
        this.handleDeleteClick = this.handleDeleteClick.bind(this)
        this.handleCreateClick = this.handleCreateClick.bind(this)
    }

    async componentDidMount() {
        await this.loadData()
    }

    async loadData() {
        const id = this.state.bookId
        if (!id) {
            console.error("Ошибка получения id книги: ", id)
            throw new Error('Ошибка получения id книги')
        }

        try {
            const response = await fetch(`/api/v1/comment/${id}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            const comments = await response.json()
            this.setState({comments})
        } catch (error) {
            console.error("Ошибка получения данных:", error)
        }
    }

    render() {
        const comments = this.state.comments
        if (!Array.isArray(comments)) {
            this.setState({comments: []})
            this.handleCreateClick(this.state.bookId)
        }

        if (comments.length === 0 && !this.state.isFirstLoad) {
            this.handleCreateClick(this.state.bookId)
        }

        if (comments.length === 0) {
            this.state.isFirstLoad = false
            return (
                <div>Загрузка...</div>
            )
        }

        return (
            <React.Fragment>
                <form>
                    <Header title={'Comments'}/>
                    <table className='persons-table'>
                        <thead>
                        <tr className='persons-table'>
                            <th className='persons-table'>Number</th>
                            <th className='persons-table'>Comments</th>
                            <th className='persons-table'>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        {
                            this.state.comments?.map((comment, i) => (
                                <tr className='persons-table_item persons-table_comments' key={i}>
                                    <td className='persons-table_item'>{i + 1}</td>
                                    <td className='persons-table_item persons-table_comments'>{comment?.commentText}</td>
                                    <td>
                                        <div className='persons-table_button'>
                                            <button type={"button"} onClick={() => this.handleDeleteClick(comment?.id)}>
                                                Удалить комментарий
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))
                        }
                        </tbody>
                    </table>
                </form>
                <table>
                    <tbody>
                    <tr>
                        <td>
                            <div className='persons-table_button'>
                                <button type={"button"} onClick={() => this.handleCreateClick(this.state.bookId)}>
                                    Добавить комментарий
                                </button>
                            </div>
                        </td>
                        <td>
                            <div className='persons-table_button'>
                                <button type="button" onClick={() => this.handleBooksClick()}>Отмена</button>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </React.Fragment>
        )
    }

    async handleDeleteClick(id) {
        try {
            const response = await fetch(`/api/v1/comment/${id}`, {
                method: 'DELETE'
            })
            if (response.ok) {
                this.setState({
                    comments: this.state.comments.filter(comment => comment.id !== id)
                })
            } else {
                console.error("Ошибка удаления комментария:", response.status)
            }
        } catch (error) {
            console.error("Ошибка удаления комментария:", error)
        }
    }

    handleCreateClick(bookId) {
        ReactDOM.render(<CommentCreatePresenter bookId={bookId}/>, document.getElementById('content'))
        this.state = {comments: [], isFirstLoad: true}
    }

    handleBooksClick() {
        ReactDOM.render(<App/>, document.getElementById('content'))
        this.state = {bookId: '', comments: [], isFirstLoad: true}
    }
}
