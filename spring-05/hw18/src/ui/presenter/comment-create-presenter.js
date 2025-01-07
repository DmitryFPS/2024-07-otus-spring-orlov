import React from 'react'
import ReactDOM from 'react-dom'
import App from './books-presenter.js'
import CommentsPresenter from './comments-presenter.js'


export default class CommentCreatePresenter extends React.Component {
    tempTitle = ''

    constructor(props) {
        super(props)
        this.state = {bookId: props.bookId}
        this.handleTitleChange = this.handleTitleChange.bind(this)
        this.handleBooksClick = this.handleBooksClick.bind(this)
        this.handleSubmit = this.handleSubmit.bind(this)
    }

    handleTitleChange(event) {
        this.tempTitle = event.target.value
    }

    async handleSubmit(event) {
        event.preventDefault()

        const bookId = this.state.bookId
        const content = this.tempTitle
        if (content === undefined || content.length === 0) {
            this.handleCommentsClick()
        }

        const data = {bookId, content}

        try {
            const response = await fetch('/api/v1/comment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })

            if (!response.ok) {
                throw new Error('Ошибка создания комментария')
            }

            console.log('Комментарий создан успешно')
            this.handleBooksClick()
        } catch (error) {
            console.error('Ошибка создания комментария:', error)
        }
    }

    render() {
        const title = this.state.title

        return (
            <React.Fragment>
                <form onSubmit={this.handleSubmit}>
                    <table className='persons-table'>
                        <thead>
                        <tr className='persons-table'>
                            <th className='persons-table'>Комментарий:</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr className='persons-table_item'>
                            <td className='persons-table_item'>
                                <label>
                                    <input className='persons-table_item__title' type="text" value={title}
                                           onChange={this.handleTitleChange}/>
                                </label>
                            </td>
                        </tr>
                        </tbody>
                        <thead>
                        <tr>
                            <td>
                                <div className='persons-table_button'>
                                    <button type="submit">Сохранить комментарий</button>
                                </div>
                            </td>
                        </tr>
                        </thead>
                    </table>
                </form>
                <table>
                    <tbody>
                    <tr>
                        <td>
                            <div className='persons-table_button'>
                                <button type="button" onClick={() => this.handleBooksClick()}>К списку книг</button>
                            </div>
                        </td>
                        <td>
                            <div className='persons-table_button'>
                                <button type="button" onClick={() => this.handleCommentsClick()}>К списку комментарий
                                </button>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </React.Fragment>
        )
    }

    handleCommentsClick() {
        ReactDOM.render(<CommentsPresenter bookId={this.state.bookId}/>, document.getElementById('content'))
        this.state = {bookId: ''}
    }

    handleBooksClick() {
        ReactDOM.render(<App/>, document.getElementById('content'))
        this.state = {bookId: ''}
    }
}
